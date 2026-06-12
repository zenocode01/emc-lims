import { useRef, useState } from 'react'
import { Button, Space, Modal, message, Card } from 'antd'
import { PlusOutlined, DeleteOutlined, DownloadOutlined, ExclamationCircleOutlined } from '@ant-design/icons'
import type { ProColumns, ActionType } from '@ant-design/pro-components'
import { ProTable } from '@ant-design/pro-components'
import { standardApi, type StandardVO, type StandardQuery } from '../../api/standard'
import { downloadBlob } from '../../utils/download'
import StandardForm from './StandardForm'

/**
 * 标准管理页面
 */
export default function StandardPage() {
  const actionRef = useRef<ActionType>(null)
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
  const [formOpen, setFormOpen] = useState(false)
  const [formData, setFormData] = useState<StandardVO | undefined>(undefined)

  /** 新增 */
  const handleAdd = () => {
    setFormData(undefined)
    setFormOpen(true)
  }

  /** 编辑 */
  const handleEdit = (record: StandardVO) => {
    setFormData(record)
    setFormOpen(true)
  }

  /** 删除 */
  const handleDelete = async (ids: number[]) => {
    Modal.confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: `确定删除选中的 ${ids.length} 个标准吗？删除后不可恢复。`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await standardApi.deleteBatch(ids)
          message.success('删除成功')
          actionRef.current?.reload()
          setSelectedRowKeys([])
        } catch {
          // error handled by interceptor
        }
      },
    })
  }

  /** 表单成功回调 */
  const handleFormSuccess = () => {
    setFormOpen(false)
    setFormData(undefined)
    actionRef.current?.reload()
  }

  /** 导出 */
  const handleExport = async () => {
    try {
      const res = await standardApi.export({})
      downloadBlob(res, '标准列表.xlsx')
      message.success('导出成功')
    } catch {
      // error handled by interceptor
    }
  }

  const columns: ProColumns<StandardVO>[] = [
    {
      title: '标准编号',
      dataIndex: 'code',
      width: 160,
      ellipsis: true,
    },
    {
      title: '标准名称',
      dataIndex: 'name',
      width: 220,
      ellipsis: true,
      render: (_, record) => <a onClick={() => handleEdit(record)}>{record.name}</a>,
    },
    {
      title: '版本号',
      dataIndex: 'version',
      width: 100,
    },
    {
      title: '发布机构',
      dataIndex: 'issuingOrg',
      width: 160,
      ellipsis: true,
    },
    {
      title: '类型',
      dataIndex: 'type',
      width: 100,
      valueType: 'select',
      valueEnum: {
        emission: { text: '排放', status: 'Warning' },
        immunity: { text: '抗扰度', status: 'Success' },
      },
      render: (_, record) => record.typeName || (record.type === 'emission' ? '排放' : '抗扰度'),
    },
    {
      title: '生效日期',
      dataIndex: 'effectiveDate',
      width: 120,
      search: false,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      valueType: 'select',
      valueEnum: {
        enabled: { text: '已启用', status: 'Success' },
        disabled: { text: '已停用', status: 'Error' },
      },
      render: (_, record) => record.statusName || (record.status === 'enabled' ? '已启用' : '已停用'),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 170,
      valueType: 'dateTime',
      search: false,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      width: 150,
      ellipsis: true,
      search: false,
    },
    {
      title: '操作',
      width: 150,
      fixed: 'right',
      search: false,
      render: (_, record) => (
        <Space>
          <a onClick={() => handleEdit(record)}>编辑</a>
          <a onClick={() => handleDelete([record.id])} style={{ color: '#ff4d4f' }}>
            删除
          </a>
        </Space>
      ),
    },
  ]

  return (
    <ProTable<StandardVO, StandardQuery>
      actionRef={actionRef}
      columns={columns}
      rowKey="id"
      request={async (params) => {
        const { current, pageSize, ...rest } = params
        const res = await standardApi.page({
          pageNum: current,
          pageSize,
          keyword: rest.keyword,
          type: rest.type,
          status: rest.status,
          effectiveDateStart: rest.effectiveDateStart,
          effectiveDateEnd: rest.effectiveDateEnd,
        })
        return {
          data: res.records,
          total: res.total,
          success: true,
        }
      }}
      toolbar={{
        actions: [
          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            新增标准
          </Button>,
          <Button
            key="export"
            icon={<DownloadOutlined />}
            onClick={handleExport}
          >
            导出Excel
          </Button>,
          <Button
            key="delete"
            danger
            icon={<DeleteOutlined />}
            disabled={selectedRowKeys.length === 0}
            onClick={() => handleDelete(selectedRowKeys as number[])}
          >
            批量删除
          </Button>,
        ],
      }}
      rowSelection={{
        selectedRowKeys,
        onChange: (keys) => setSelectedRowKeys(keys),
      }}
      pagination={{
        showSizeChanger: true,
        showQuickJumper: true,
        pageSizeOptions: ['10', '20', '50'],
      }}
      search={{
        labelWidth: 'auto',
        defaultCollapsed: true,
      }}
      dateFormatter="string"
    />

    {/* 新增/编辑弹窗 */}
    <StandardForm
      open={formOpen}
      data={formData}
      onSuccess={handleFormSuccess}
      onCancel={() => {
        setFormOpen(false)
        setFormData(undefined)
      }}
    />
  )
}
