import { useRef, useState } from 'react'
import { Button, Space, Tag, Modal, message, Card } from 'antd'
import {
  PlusOutlined,
  DeleteOutlined,
  DownloadOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons'
import type { ProColumns, ActionType } from '@ant-design/pro-components'
import { ProTable } from '@ant-design/pro-components'
import {
  personnelApi,
  type PersonnelVO,
  type PersonnelQuery,
} from '../../api/personnel'
import PersonnelForm from './PersonnelForm'

/**
 * 人员管理页面
 */
export default function PersonnelPage() {
  const actionRef = useRef<ActionType>(null)
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
  const [formOpen, setFormOpen] = useState(false)
  const [formData, setFormData] = useState<PersonnelVO | undefined>(undefined)

  /** 新增 */
  const handleAdd = () => {
    setFormData(undefined)
    setFormOpen(true)
  }

  /** 编辑 */
  const handleEdit = (record: PersonnelVO) => {
    setFormData(record)
    setFormOpen(true)
  }

  /** 删除 */
  const handleDelete = async (ids: number[]) => {
    Modal.confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: `确定删除选中的 ${ids.length} 个人员吗？删除后不可恢复。`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await personnelApi.deleteBatch(ids)
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

  /** 获取资质状态标签 */
  const getStatusTag = (record: PersonnelVO) => {
    // 根据资质有效期判断状态
    const now = new Date()
    if (!record.hireDate) {
      return <Tag color="default">--</Tag>
    }
    return <Tag color="success">有效</Tag>
  }

  const columns: ProColumns<PersonnelVO>[] = [
    {
      title: '姓名',
      dataIndex: 'name',
      width: 120,
      ellipsis: true,
      render: (_, record) => <a onClick={() => handleEdit(record)}>{record.name}</a>,
    },
    {
      title: '工号',
      dataIndex: 'userId',
      width: 100,
      search: false,
    },
    {
      title: '职称',
      dataIndex: 'title',
      width: 120,
      valueType: 'select',
      valueEnum: {
        senior: { text: '高级职称', status: 'Gold' },
        intermediate: { text: '中级职称', status: 'Processing' },
        junior: { text: '初级职称', status: 'Default' },
      },
      search: true,
    },
    {
      title: '学历',
      dataIndex: 'education',
      width: 100,
      valueType: 'select',
      valueEnum: {
        doctoral: { text: '博士', status: 'Success' },
        master: { text: '硕士', status: 'Success' },
        bachelor: { text: '本科', status: 'Processing' },
        college: { text: '大专', status: 'Default' },
      },
      search: true,
    },
    {
      title: '身份证号',
      dataIndex: 'idCard',
      width: 180,
      ellipsis: true,
      search: false,
    },
    {
      title: '入职日期',
      dataIndex: 'hireDate',
      width: 130,
      valueType: 'date',
      search: true,
      sorter: true,
    },
    {
      title: '资质状态',
      dataIndex: 'status',
      width: 100,
      search: true,
      valueEnum: {
        valid: { text: '有效', status: 'Success' },
        expiring_30days: { text: '即将过期', status: 'Warning' },
        expired: { text: '已过期', status: 'Error' },
      },
      render: (_, record) => getStatusTag(record),
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      search: true,
      valueEnum: {
        1: { text: '启用', status: 'Success' },
        0: { text: '停用', status: 'Error' },
      },
      render: (_, record) =>
        record.status === '1' ? (
          <Tag color="success">启用</Tag>
        ) : record.status === '0' ? (
          <Tag color="error">停用</Tag>
        ) : (
          <Tag color="default">--</Tag>
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
      width: 120,
      fixed: 'right',
      search: false,
      render: (_, record) => (
        <Space>
          <a onClick={() => handleEdit(record)}>编辑</a>
          <a
            onClick={() => handleDelete([record.id])}
            style={{ color: '#ff4d4f' }}
          >
            删除
          </a>
        </Space>
      ),
    },
  ]

  return (
    <Card>
      <ProTable<PersonnelVO, PersonnelQuery>
        actionRef={actionRef}
        columns={columns}
        rowKey="id"
        request={async (params) => {
          const { current, pageSize, ...rest } = params
          const res = await personnelApi.page({
            pageNum: current,
            pageSize,
            keyword: rest.keyword,
            education: rest.education,
            title: rest.title,
            status: rest.status,
            hireDateStart: rest.hireDateStart,
            hireDateEnd: rest.hireDateEnd,
          })
          return {
            data: res.records,
            total: res.total,
            success: true,
          }
        }}
        toolbar={{
          actions: [
            <Button
              key="add"
              type="primary"
              icon={<PlusOutlined />}
              onClick={handleAdd}
            >
              新增人员
            </Button>,
            <Button
              key="export"
              icon={<DownloadOutlined />}
              onClick={() =>
                personnelApi.export({
                  keyword: undefined,
                  education: undefined,
                  title: undefined,
                  status: undefined,
                  hireDateStart: undefined,
                  hireDateEnd: undefined,
                  pageNum: 1,
                  pageSize: 1000,
                })
              }
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
      <PersonnelForm
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
