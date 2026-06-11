import { useRef, useState } from 'react'
import { Button, Space, Tag, Modal, message, Switch, Card } from 'antd'
import { PlusOutlined, DeleteOutlined, DownloadOutlined, ExclamationCircleOutlined } from '@ant-design/icons'
import type { ProColumns, ActionType } from '@ant-design/pro-components'
import { ProTable } from '@ant-design/pro-components'
import { customerApi, type CustomerVO, type CustomerQuery } from '../../api/customer'
import { downloadBlob } from '../../utils/download'
import CustomerForm from './CustomerForm'

/**
 * 客户管理页面
 */
export default function CustomerPage() {
  const actionRef = useRef<ActionType>(null)
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
  const [formOpen, setFormOpen] = useState(false)
  const [formData, setFormData] = useState<CustomerVO | undefined>(undefined)

  /** 新增 */
  const handleAdd = () => {
    setFormData(undefined)
    setFormOpen(true)
  }

  /** 编辑 */
  const handleEdit = (record: CustomerVO) => {
    setFormData(record)
    setFormOpen(true)
  }

  /** 删除 */
  const handleDelete = async (ids: number[]) => {
    Modal.confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: `确定删除选中的 ${ids.length} 个客户吗？删除后不可恢复。`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await customerApi.deleteBatch(ids)
          message.success('删除成功')
          actionRef.current?.reload()
          setSelectedRowKeys([])
        } catch {
          // error handled by interceptor
        }
      },
    })
  }

  /** 修改状态 */
  const handleStatusChange = async (id: number, status: number) => {
    try {
      await customerApi.updateStatus(id, status)
      message.success(status === 1 ? '已启用' : '已禁用')
      actionRef.current?.reload()
    } catch {
      // error handled by interceptor
    }
  }

  /** 表单成功回调 */
  const handleFormSuccess = () => {
    setFormOpen(false)
    setFormData(undefined)
    actionRef.current?.reload()
  }

  const columns: ProColumns<CustomerVO>[] = [
    {
      title: '客户名称',
      dataIndex: 'name',
      width: 180,
      ellipsis: true,
      render: (_, record) => <a onClick={() => handleEdit(record)}>{record.name}</a>,
    },
    {
      title: '客户类型',
      dataIndex: 'type',
      width: 100,
      valueType: 'select',
      valueEnum: {
        1: { text: '企业', status: 'Success' },
        2: { text: '个人', status: 'Default' },
      },
      render: (_, record) => record.typeName || (record.type === 1 ? '企业' : '个人'),
    },
    {
      title: '行业',
      dataIndex: 'industry',
      width: 120,
      ellipsis: true,
    },
    {
      title: '联系人',
      dataIndex: 'contact',
      width: 100,
    },
    {
      title: '联系电话',
      dataIndex: 'phone',
      width: 130,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      valueType: 'select',
      valueEnum: {
        1: { text: '启用', status: 'Success' },
        0: { text: '禁用', status: 'Error' },
      },
      render: (_, record) => (
        <Switch
          checked={record.status === 1}
          size="small"
          onChange={(checked) => handleStatusChange(record.id, checked ? 1 : 0)}
        />
      ),
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
    <Card>
      <ProTable<CustomerVO, CustomerQuery>
        actionRef={actionRef}
        columns={columns}
        rowKey="id"
        request={async (params) => {
          const { current, pageSize, ...rest } = params
          const res = await customerApi.page({
            pageNum: current,
            pageSize,
            keyword: rest.keyword,
            type: rest.type,
            industry: rest.industry,
            status: rest.status,
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
              新增客户
            </Button>,
            <Button
              key="export"
              icon={<DownloadOutlined />}
              onClick={() => window.open('/api/customer/export', '_blank')}
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
      <CustomerForm
        open={formOpen}
        data={formData}
        onSuccess={handleFormSuccess}
        onCancel={() => {
          setFormOpen(false)
          setFormData(undefined)
        }}
      />
    </Card>
  )
}
